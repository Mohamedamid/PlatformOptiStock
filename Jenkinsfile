pipeline {
    // Agent 'any' is used, requiring Maven and a compatible JDK (e.g., JDK 17)
    // to be pre-installed on the Jenkins agent machine.
    agent any

    environment {
        // You can define variables here, for example, your SonarQube project name or specific paths.
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from GitHub..."
                git branch: 'dev',
                    credentialsId: 'optickToken', // Uses the GitHub Token ID
                    url: 'https://github.com/Mohamedamid/PlatformOptiStock.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building the Java/Maven project (Skipping Tests)..."
                // Compile and package the project dependencies.
                sh "mvn clean install -DskipTests"
            }
        }

        stage('Tests & Coverage') {
            steps {
                echo "Running Unit Tests and generating Surefire reports..."
                // Execute unit tests. Surefire automatically generates XML reports.
                sh "mvn test"

                // Post-process the XML reports so Jenkins can display test results.
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('SonarQube Analysis') {
            steps { // <--- التعديل هنا: إضافة block ديال steps
                // This pulls the Secret Text Credential 'SonarToken' and puts its value into $SONAR_LOGIN_TOKEN
                withCredentials([string(credentialsId: 'SonarToken', variable: 'SONAR_LOGIN_TOKEN')]) {
                    steps {
                        echo "Starting SonarQube analysis..."
                        withSonarQubeEnv('SonarQube') {
                            // Run the build, verification, and sonar goal in one command.
                            sh """
                            mvn clean verify sonar:sonar \
                            -Dsonar.projectKey=api-logistique \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.login=$SONAR_LOGIN_TOKEN
                            """
                        }
                    }
                }
            } // <--- الإغلاق ديال block ديال steps
        }

        stage('Quality Gate Check') {
            steps {
                echo "Waiting for SonarQube Quality Gate result..."
                // Wait for up to 15 minutes for SonarQube to finish and check the Quality Gate.
                timeout(time: 15, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package & Archive') {
            steps {
                echo "Archiving the final JAR artifact..."
                // The 'mvn package' command ensures the final JAR is created
                sh "mvn package"
                // Archive the generated JAR file in the Jenkins build record.
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy (Optional)') {
            steps {
                sh "echo Deploying the packaged artifact to a Staging/Prod environment..."
            }
        }
    }
}