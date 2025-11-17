pipeline {
    // Agent 'any' is used, requiring a compatible JDK (e.g., JDK 17) to be pre-installed.
    agent any

    /*
    environment {
        // تم حذف هذا الجزء سابقاً لأنه كان فارغاً ويتسبب في خطأ بناء (Syntax Error).
        // يمكنك إضافته هنا وتعريف متغيرات البيئة إذا كنت بحاجة إليها لاحقاً.
        // مثال: JAVA_HOME = "${tool 'JDK-17'}"
    }
    */

    tools {
        // الاسم الذي تم تأكيده من الإعدادات العامة
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
                // استخدام 'bat' بدل 'sh' لأن الوكيل يعمل على Windows
                bat "mvn clean install -DskipTests"
            }
        }

        stage('Tests & Coverage') {
            steps {
                echo "Running Unit Tests and generating Surefire reports..."
                bat "mvn test"

                // نشر نتائج الاختبار
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // هذا هو المكان الذي كان فيه block 'steps' الزائد، وقد تم حذفه لتفادي خطأ الـSyntax.
                withCredentials([string(credentialsId: 'SonarToken', variable: 'SONAR_LOGIN_TOKEN')]) {
                    // أوامر SonarQube مباشرة هنا
                    withSonarQubeEnv('SonarJenkins') {
                        // استخدام 'bat' وأوامر Windows
                        bat """
                        mvn clean verify sonar:sonar ^
                        -Dsonar.projectKey=api-logistique ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.login=%SONAR_LOGIN_TOKEN%
                        """
                    }
                }
            }
        }

        stage('Quality Gate Check') {
            steps {
                echo "Waiting for SonarQube Quality Gate result..."
                // الانتظار على نتيجة SonarQube
                timeout(time: 15, unit: 'MINUTES') {
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