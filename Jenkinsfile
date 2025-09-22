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
        stage('Code Coverage') {
            steps {
                // Publish JaCoCo coverage report using modern coverage API
                publishCoverage(
                    adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')],
                    sourceFileResolver: sourceFiles('NEVER_STORE')
                )

                // Archive the HTML coverage report
                archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
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
            // Publish JUnit test results
            junit 'target/surefire-reports/**/*.xml'

            // Optional: Record coverage trends (alternative approach)
            recordCoverage(
                tools: [
                    [tool: jacoco(pattern: 'target/site/jacoco/jacoco.xml')]
                ]
            )
        }
    }
}