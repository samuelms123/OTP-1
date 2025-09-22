pipeline {
    agent any
    environment {
        SALT_ROUNDS = '7'
        JWT_SECRET = credentials('secret-secret')
    }
    stages {
        stage('Create .env file') {
            steps {
                bat '''
                    echo Creating .env file...
                    echo SALT_ROUNDS=%SALT_ROUNDS% > .env
                    echo JWT_SECRET=%JWT_SECRET% >> .env
                    type .env
                '''
            }
        }
        stage('Build and Test') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Cleanup') {
            steps {
                bat 'del .env'
            }
        }
    }
}