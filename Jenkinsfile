pipeline {
    agent any

    tools {
        maven 'Maven3916'
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
                    --scan './'
                    --out './'
                    --format 'ALL'
                    --prettyPrint''', odcInstallation: 'OWASP-DepCheck-10'
                dependencyCheckPublisher failedTotalCritical: 1, pattern: 'dependency-check-report.xml', stopBuild: true
            }
        }
    }
}