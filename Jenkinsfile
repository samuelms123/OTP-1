pipeline {
    agent any
    environment {
        SALT_ROUNDS = '7'
        JWT_SECRET = credentials('secret-secret')
    }
    stages {
        stage('Create .env file') {
            steps {
                powershell '''
                    Write-Output "Creating .env file..."
                    "SALT_ROUNDS=$env:SALT_ROUNDS" | Out-File -FilePath .env -Encoding ASCII
                    "JWT_SECRET=$env:JWT_SECRET" | Out-File -FilePath .env -Encoding ASCII -Append
                    Get-Content .env
                '''
            }
        }
        stage('Verify .env') {
            steps {
                bat '''
                    echo Checking if .env file exists...
                    if exist .env (
                        echo .env file found!
                        type .env
                    ) else (
                        echo .env file NOT found!
                    )
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
                powershell 'Remove-Item .env -Force -ErrorAction SilentlyContinue'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
            junit 'target/surefire-reports/**/*.xml'

            jacoco(
                execPattern: 'target/jacoco.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java',
                exclusionPattern: 'src/test/*',
                skipCopyOfSrcFiles: false,
                skipFailedBuild: true
            )
        }
        success {
            echo 'Build and tests completed successfully!'
        }
        failure {
            echo 'Build or tests failed!'
        }
    }
}