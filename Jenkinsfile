pipeline {
    agent any
    environment {
        SALT_ROUNDS = '7'
        JWT_SECRET = credentials('secret-secret')
    }
    stages {
        stage('Build') {
            steps {
                sh '''
                    echo "SALT_ROUNDS=$SALT_ROUNDS"
                    echo "JWT_SECRET=$JWT_SECRET"
                '''
            }
        }
    }
}