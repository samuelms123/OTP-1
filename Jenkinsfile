pipeline {
    agent any
    environment {
        SALT_ROUNDS = '7'
        JWT_SECRET = credentials('secret-secret')
    }
    stages {
        stage('Setup') {
            steps {
                sh '''
                    # Create .env file for the application
                    echo "SALT_ROUNDS=$SALT_ROUNDS" > .env
                    echo "JWT_SECRET=$JWT_SECRET" >> .env
                    chmod 600 .env  # Secure the file
                '''
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Cleanup') {
            steps {
                sh 'rm -f .env'  # Remove sensitive file after tests
            }
        }
    }
}