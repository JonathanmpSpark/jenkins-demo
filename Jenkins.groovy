pipeline {
  environment {
    registry = "jonathanmpspark/jenkins-demo"
    registryCredential = 'dockerhub-demo-01'
    dockerImage = ''
    systemVersion = "latest"
    dockerLabel = ''
  }
  agent any
  stages {
    stage('Getting latest changes') {
      steps {
        git(
            url: 'https://github.com/JonathanmpSpark/jenkins-demo',
            credentialsId: 'github-demo-01',
            branch: "staging"
        )
        script {
            def config = readJSON file: './version.json'
            systemVersion = "${config.AppVersion}"
            def digits = systemVersion.tokenize('.')
            config['AppVersion'] = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"
            dockerLabel = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"

            print dockerLabel
            // writeJSON file: './version.json', json: config
        }
      }
    }
  }
}