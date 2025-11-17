pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from GitHub..."
                git branch: 'dev',
                    credentialsId: 'optickToken',
                    url: 'https://github.com/Mohamedamid/PlatformOptiStock.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building the Java/Maven project (Skipping Tests)..."
                bat "mvn clean install -DskipTests"
            }
        }

        stage('Tests & Coverage') {
            steps {
                echo "Running Unit Tests and generating Surefire reports..."
                bat "mvn test"

                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SonarToken', variable: 'SONAR_LOGIN_TOKEN')]) {
                    withSonarQubeEnv('SonarJenkins') {
                        bat """
                        mvn clean verify sonar:sonar ^
                        -Dsonar.projectKey=api-logistique ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.login=%SONAR_LOGIN_TOKEN% ^
                        -Djacoco.check.skip=true
                        """
                    }
                }
            }
        }

        stage('Quality Gate Check') {
            steps {
                echo "Waiting for SonarQube Quality Gate result..."
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package & Archive') {
            steps {
                echo "Archiving the final JAR artifact..."
                bat "mvn package"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy (Optional)') {
            steps {
                echo "Deployment steps go here..."
                bat "echo Deployment successful."
            }
        }
    }
}