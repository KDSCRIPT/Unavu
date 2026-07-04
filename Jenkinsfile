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
        stage('Build Maven Dependencies') {
            options { timestamps()}
            steps {
                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
    }
}