pipeline {
  environment {
    registry = "jonathanmpspark/jenkins-demo"
    registryCredential = 'demo-dockerhub'
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
            credentialsId: 'demo-github',
            branch: "staging"
        )
        script {
            def config = readJSON file: './src/version.json'
            systemVersion = "${config.AppVersion}"
            def digits = systemVersion.tokenize('.')
            config['AppVersion'] = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"
            dockerLabel = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"
            // writeJSON file: './version.json', json: config
        }
      }
    }

    stage("Display container tag..."){
      steps{
        echo "Container tag: "  + dockerLabel
      }
    }

    stage("Display build number..."){
      steps{
        echo "Build number: "  + "$BUILD_NUMBER"
      }
    }

    
  }
}