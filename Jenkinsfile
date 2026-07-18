pipeline {
    agent any

    tools {
        maven 'Maven3916'
    }

    environment {
        NVD_API_KEY=credentials('nvd-api-key')
        SONARQUBE_TOKEN=credentials('sonarqube-server-token')
        GITEA_TOKEN=credentials('gitea-api-token')
    }
    options {
        disableResume()
        disableConcurrentBuilds abortPrevious: true
    }

    stages {
        stage('Build Maven Dependencies skipping Tests') {
            options { timestamps()}
            steps {
                script {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('OWASP Dependency Scanning') {
            steps {
                dependencyCheck additionalArguments: '''
                    --nvdApiKey $NVD_API_KEY
                    --scan './'
                    --out './'
                    --format 'HTML'
                    --format 'XML'
                    --format 'JUNIT'
                    --prettyPrint''', odcInstallation: 'OWASP-DepCheck-10'
                //dependencyCheckPublisher failedTotalCritical: 1, pattern: 'dependency-check-report.xml' ,stopBuild: true
            }
        }

        stage('Unit Testing') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('SAST - SonarQube') {
            steps {
                timeout(time:300, unit:'SECONDS') {
                sh '''
                    mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=Unavu \
                        -Dsonar.projectName='Unavu' \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=$SONARQUBE_TOKEN \
                        -Dsonar.qualitygate.wait=true
                        -DskipTests
                '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                mvn com.google.cloud.tools:jib-maven-plugin:3.4.0:dockerBuild \
                -Djib.to.tags=$GIT_COMMIT \
                -DskipTests 
                '''
            }
        }

        stage('Trivy Vulnerability Scanning') {
            steps {
                script {
                    def services = [
                        'restaurant', 'user', 'list', 'review', 'social-graph',
                        'config-server', 'gateway-server', 'notification', 'feed', 'activity'
                    ]
                    services.each { svc ->
                        sh """
                            trivy image containedtogether/${svc}:$GIT_COMMIT \
                                --severity LOW,MEDIUM,HIGH \
                                --exit-code 0 --quiet \
                                --format json -o trivy-${svc}-MEDIUM-results.json

                            trivy image containedtogether/${svc}:$GIT_COMMIT \
                                --severity CRITICAL \
                                --exit-code 0 --quiet \
                                --format json -o trivy-${svc}-CRITICAL-results.json
                        """
                    }
                }
            }
            post {
                always {
                    script {
                        def services = [
                            'restaurant', 'user', 'list', 'review', 'social-graph',
                            'config-server', 'gateway-server', 'notification', 'feed', 'activity'
                        ]
                        services.each { svc ->
                            sh """
                                trivy convert --format template --template "@/usr/local/share/trivy/templates/html.tpl" \
                                    --output trivy-${svc}-MEDIUM-results.html trivy-${svc}-MEDIUM-results.json
                                trivy convert --format template --template "@/usr/local/share/trivy/templates/html.tpl" \
                                    --output trivy-${svc}-CRITICAL-results.html trivy-${svc}-CRITICAL-results.json
                            """
                        }
                    }
                }
            }
        }

        stage('Push Docker Images to DockerHub') {
            steps {
                withDockerRegistry(credentialsId: 'docker-hub-credentials', url: 'https://index.docker.io/v1/') {
                    script {
                        def services = [
                            'restaurant', 'user', 'list', 'review', 'social-graph',
                            'config-server', 'gateway-server', 'notification', 'feed', 'activity'
                        ]
                        services.each { svc ->
                            sh "docker push containedtogether/${svc}:${GIT_COMMIT}"
                        }
                    }
                }
            }
        }

        stage('Deploy to Development Environment') {
            when {
                branch 'feature/*'
            }
            steps {
                checkout scmGit(branches: [[name: 'helm']], extensions: [[$class: 'CleanBeforeCheckout']], userRemoteConfigs: [[credentialsId: 'Gitea-Credentials', url: 'http://172.20.217.56:3000/adminaccount/Unavu']])
                sh '''
                for d in unavu-common/ unavu-services/*/; do
                    [ -d "$d" ] || continue
                    echo "=== $d ==="
                    rm -f "${d}Chart.lock"
                    rm -rf "${d}charts"
                    (cd "$d" && helm dependency update)
                done
                '''
                withCredentials([file(credentialsId: 'secrets-dev-yaml', variable: 'DEV_SECRETS_FILE')]) {
                    sh """
                        rm -f ./environments/secrets.dev.yaml
                        cp "$DEV_SECRETS_FILE" ./environments/secrets.dev.yaml
                        cd deploy
                        helmfile -e dev --state-values-set IMAGE_TAG="${GIT_COMMIT}" sync
                    """
                }
            }
        }

         stage('Approve QA Deployment') {
            when {
                branch 'feature/*'
            }
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input message: 'Dev is up. Approve deployment to QA? (This will tear down Dev first)', 
                          ok: 'Yes! Tear down Dev and Deploy to QA', 
                          submitter: 'admin'
                }
            }
        }
 
        stage('Tear Down Development Environment') {
            when {
                branch 'feature/*'
            }
            steps {
                sh '''
                    kubectl delete namespace dev --ignore-not-found
                    kubectl wait --for=delete namespace/dev --timeout=180s || true
                    while kubectl get namespace dev >/dev/null 2>&1; do
                        echo "Waiting for namespace dev to fully terminate..."
                        sleep 5
                    done
                '''
            }
        }
 
        stage('Deploy to QA Environment') {
            when {
                branch 'feature/*'
            }
            steps {
                checkout scmGit(branches: [[name: 'helm']], extensions: [[$class: 'CleanBeforeCheckout']], userRemoteConfigs: [[credentialsId: 'Gitea-Credentials', url: 'http://172.20.217.56:3000/adminaccount/Unavu']])
                withCredentials([file(credentialsId: 'secrets-qa-yaml', variable: 'QA_SECRETS_FILE')]) {
                    sh '''
                        rm -f ./environments/secrets.qa.yaml
                        cp "$QA_SECRETS_FILE" ./environments/secrets.qa.yaml
                    '''
                    sh """
                        cd deploy
                        helmfile -e qa --state-values-set IMAGE_TAG="${GIT_COMMIT}" sync
                    """
                }
            }
        }
 
        stage('Raise PR to Main') {
            when {
                branch 'feature/*'
            }
            steps {
                sh """
                    curl -X 'POST' \
                        'http://172.20.217.56:3000/api/v1/repos/adminaccount/Unavu/pulls' \
                        -H 'accept: application/json' \
                        -H 'Authorization: token $GITEA_TOKEN' \
                        -H 'Content-Type: application/json' \
                        -d '{
                            "assignee": "adminaccount",
                            "assignees": ["adminaccount"],
                            "base": "main",
                            "body": "QA validated. Requesting merge to main for production deployment. Image tag: ${GIT_COMMIT}",
                            "head": "${GIT_BRANCH.replaceAll("origin/", "")}",
                            "title": "Release to Production - ${GIT_COMMIT.take(8)}"
                        }'
                """
            }
        }
 
        stage('Wait for PR Merge & Approve Production Deployment') {
            when {
                branch 'main'
            }
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input message: 'PR has been merged to main. Approve Production deployment? (This will tear down QA first)', 
                          ok: 'Yes! Tear down QA and Deploy to Production', 
                          submitter: 'admin'
                }
            }
        }
 
        stage('Tear Down QA Environment') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    kubectl delete namespace qa --ignore-not-found
                    kubectl wait --for=delete namespace/qa --timeout=180s || true
                    while kubectl get namespace qa >/dev/null 2>&1; do
                        echo "Waiting for namespace qa to fully terminate..."
                        sleep 5
                    done
                '''
            }
        }
 
        stage('Deploy to Production Environment') {
            when {
                branch 'main'
            }
            steps {
                checkout scmGit(branches: [[name: 'helm']], extensions: [[$class: 'CleanBeforeCheckout']], userRemoteConfigs: [[credentialsId: 'Gitea-Credentials', url: 'http://172.20.217.56:3000/adminaccount/Unavu']])
                withCredentials([file(credentialsId: 'secrets-prod-yaml', variable: 'PROD_SECRETS_FILE')]) {
                    sh '''
                        rm -f ./environments/secrets.prod.yaml
                        cp "$PROD_SECRETS_FILE" ./environments/secrets.prod.yaml
                    '''
                    sh """
                        cd deploy
                        helmfile -e prod --state-values-set IMAGE_TAG="${GIT_COMMIT}" sync
                    """
                }
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/dependency-check-report.html, **/dependency-check-report.xml, **/dependency-check-junit.xml', allowEmptyArchive: true
            
            junit allowEmptyResults: true, keepProperties: true, testResults: '**/target/surefire-reports/*.xml'

            recordCoverage(
                tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/index.html']],
                id: 'jacoco', name: 'JaCoCo Coverage',
                sourceCodeRetention: 'LAST_BUILD'
            )
            
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: './', reportFiles: 'trivy-*-CRITICAL-results.html', reportName: 'Trivy Image Critical Vul Report', reportTitles: '', useWrapperFileDirectly: true])

            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: './', reportFiles: 'trivy-*-MEDIUM-results.html', reportName: 'Trivy Image Medium Vul Report', reportTitles: '', useWrapperFileDirectly: true])
        }
    }
}