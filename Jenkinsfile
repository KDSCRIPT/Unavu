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
                    sh 'mvn clean package -DskipTests'
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
                timeout(time:60, unit:'SECONDS') {
                    sh '''
                         mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar   
                         -Dsonar.projectKey=Unavu   
                         -Dsonar.projectName='Unavu'   
                         -Dsonar.host.url=http://localhost:9000   
                         -Dsonar.token=$SONARQUBE_TOKEN
                         -Dsonar.qualitygate.wait=true
                    '''
                }
            }
        }

        stage('Build Docker Image with Dev Profile') {
            steps {
                sh '''
                mvn com.google.cloud.tools:jib-maven-plugin:3.4.0:dockerBuild 
                -Djib.to.tags=$GIT_COMMIT
                -DskipTests 
                -Pdev
                '''
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/dependency-check-report.html, **/dependency-check-report.xml, **/dependency-check-junit.xml', allowEmptyArchive: true
            junit allowEmptyResults: true, keepProperties: true, testResults: '**/target/surefire-reports/*.xml'
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, icon: '', keepAll: true, reportDir: '**/target/site/jacoco', reportFiles: 'index.html', reportName: 'JaCoCo Coverage Report', reportTitles: '', useWrapperFileDirectly: true])

        }
    }
}