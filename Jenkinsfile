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
            when { branch 'feature/*' }
            options { timestamps() }
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('OWASP Dependency Scanning') {
            when { branch 'feature/*' }
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
            when { branch 'feature/*' }
            steps {
                sh 'mvn test'
            }
        }

        stage('Code Coverage') {
            when { branch 'feature/*' }
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('SAST - SonarQube') {
            when { branch 'feature/*' }
            steps {
                timeout(time: 300, unit: 'SECONDS') {
                    sh '''
                        mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                            -Dsonar.projectKey=Unavu \
                            -Dsonar.projectName='Unavu' \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.token=$SONARQUBE_TOKEN \
                            -Dsonar.qualitygate.wait=true \
                            -DskipTests
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            when { branch 'feature/*' }
            steps {
                script {
                    def services = [
                        'restaurant', 'user', 'list', 'review', 'social-graph',
                        'config-server', 'gateway-server', 'notification', 'feed', 'activity'
                    ]
                    services.each { svc ->
                        sh """
                            mvn com.google.cloud.tools:jib-maven-plugin:3.4.0:dockerBuild \
                                -Djib.to.image=containedtogether/${svc}:$GIT_COMMIT \
                                -DskipTests
                        """
                    }
                }
            }
        }

        stage('Trivy Vulnerability Scanning') {
            when { branch 'feature/*' }
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
            when { branch 'feature/*' }
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
            when { branch 'feature/*' }
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
            when { branch 'feature/*' }
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input message: 'Dev is up. Approve deployment to QA? (This will tear down Dev first)',
                          ok: 'Yes! Tear down Dev and Deploy to QA',
                          submitter: 'admin'
                }
            }
        }

        stage('Tear Down Development Environment') {
            when { branch 'feature/*' }
            steps {
                withCredentials([file(credentialsId: 'secrets-dev-yaml', variable: 'DEV_SECRETS_FILE')]) {
                    sh """
                        rm -f ./environments/secrets.dev.yaml
                        cp "$DEV_SECRETS_FILE" ./environments/secrets.dev.yaml
                        cd deploy
                        helmfile -e dev --state-values-set IMAGE_TAG="${GIT_COMMIT}" destroy
                    """
                }
            }
        }

        stage('Deploy to QA Environment') {
            when { branch 'feature/*' }
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
            when { branch 'feature/*' }
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

        stage('Resolve QA-tested Image Tag') {
            when { branch 'main' }
            steps {
                script {
                    def prJson = sh(
                        script: """
                            curl -s -H 'Authorization: token $GITEA_TOKEN' \
                            'http://172.20.217.56:3000/api/v1/repos/adminaccount/Unavu/pulls?state=closed&sort=recentupdate&limit=5&type=pulls'
                        """,
                        returnStdout: true
                    ).trim()

                    def prs = readJSON text: prJson
                    def mergedPr = prs.find { it.merged == true && it.base?.ref == 'main' }

                    if (!mergedPr) {
                        error("Could not find a recently merged PR targeting main to recover the QA-tested image tag from.")
                    }

                    def matcher = (mergedPr.body =~ /Image tag:\s*(\w+)/)
                    if (!matcher) {
                        error("Merged PR body did not contain an 'Image tag: <sha>' marker. PR: ${mergedPr.html_url}")
                    }

                    env.QA_IMAGE_TAG = matcher[0][1]
                    echo "Resolved QA-tested image tag: ${env.QA_IMAGE_TAG} (from PR #${mergedPr.number})"
                }
            }
        }

        stage('Promote Images for Production') {
            when { branch 'main' }
            steps {
                withDockerRegistry(credentialsId: 'docker-hub-credentials', url: 'https://index.docker.io/v1/') {
                    script {
                        def services = [
                            'restaurant', 'user', 'list', 'review', 'social-graph',
                            'config-server', 'gateway-server', 'notification', 'feed', 'activity'
                        ]
                        services.each { svc ->
                            sh """
                                docker pull containedtogether/${svc}:${env.QA_IMAGE_TAG}
                                docker tag containedtogether/${svc}:${env.QA_IMAGE_TAG} containedtogether/${svc}:${GIT_COMMIT}
                                docker push containedtogether/${svc}:${GIT_COMMIT}
                            """
                        }
                    }
                }
            }
        }

        stage('Wait for PR Merge & Approve Production Deployment') {
            when { branch 'main' }
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input message: 'PR has been merged to main and images promoted. Approve deployment to Production? (This will tear down QA first)',
                          ok: 'Yes! Tear down QA and Deploy to Production',
                          submitter: 'admin'
                }
            }
        }

        stage('Tear Down QA Environment') {
            when { branch 'main' }
            steps {
                withCredentials([file(credentialsId: 'secrets-qa-yaml', variable: 'QA_SECRETS_FILE')]) {
                    sh """
                        rm -f ./environments/secrets.qa.yaml
                        cp "$QA_SECRETS_FILE" ./environments/secrets.qa.yaml
                        cd deploy
                        helmfile -e qa --state-values-set IMAGE_TAG="${env.QA_IMAGE_TAG}" destroy
                    """
                }
            }
        }

        stage('Deploy to Production Environment') {
            when { branch 'main' }
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
                withCredentials([file(credentialsId: 'secrets-prod-yaml', variable: 'PROD_SECRETS_FILE')]) {
                    sh """
                        rm -f ./environments/secrets.prod.yaml
                        cp "$PROD_SECRETS_FILE" ./environments/secrets.prod.yaml
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