pipeline {
    agent any

    tools {
        maven 'Maven3916'
    }

    environment {
        NVD_API_KEY=credentials('nvd-api-key')
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
                dependencyCheckPublisher failedTotalCritical: 1, pattern: 'dependency-check-report.xml', stopBuild: true
            }
        }

        stage('Unit Testing') {
            steps {
                sh 'mvn test'
            }
        }

    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/dependency-check-report.html, **/dependency-check-report.xml, **/dependency-check-junit.xml', allowEmptyArchive: true
            junit allowEmptyResults: true, keepProperties: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}