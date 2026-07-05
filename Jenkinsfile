pipeline {
    agent any

    tools {
        maven 'Maven3916'
    }

    environment {
        NVD_API_KEY=credentials('nvd-api-key')
        SONARQUBE_TOKEN=credentials('sonarqube-server-token')        
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
                timeout(time:180, unit:'SECONDS') {
                sh '''
                    mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=Unavu \
                        -Dsonar.projectName='Unavu' \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=$SONARQUBE_TOKEN \
                        -Dsonar.qualitygate.wait=true
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
                                trivy convert --format template --template "@/usr/local/share/trivy/templates/junit.tpl" \
                                    --output trivy-${svc}-MEDIUM-results.xml trivy-${svc}-MEDIUM-results.json
                                trivy convert --format template --template "@/usr/local/share/trivy/templates/junit.tpl" \
                                    --output trivy-${svc}-CRITICAL-results.xml trivy-${svc}-CRITICAL-results.json
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

        
    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/dependency-check-report.html, **/dependency-check-report.xml, **/dependency-check-junit.xml', allowEmptyArchive: true
            
            junit allowEmptyResults: true, keepProperties: true, testResults: '**/target/surefire-reports/*.xml'
            
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: '**/target/site/jacoco', reportFiles: 'index.html', reportName: 'JaCoCo Coverage Report', reportTitles: '', useWrapperFileDirectly: true])

            junit allowEmptyResults: true, keepProperties: true, testResults: 'trivy-*-CRITICAL-results.xml'
            
            junit allowEmptyResults: true, keepProperties: true, testResults: 'trivy-*-MEDIUM-results.xml'

            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: './', reportFiles: 'trivy-*-CRITICAL-results.html', reportName: 'Trivy Image Critical Vul Report', reportTitles: '', useWrapperFileDirectly: true])

            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: './', reportFiles: 'trivy-*-MEDIUM-results.html', reportName: 'Trivy Image Medium Vul Report', reportTitles: '', useWrapperFileDirectly: true])
        }
    }
}