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
    stage('Getting last changes from github') {
      steps {
        git(
            url: 'https://github.com/JonathanmpSpark/jenkins-demo',
            credentialsId: 'demo-github',
            branch: "staging"
        )
        script {
            def config = readJSON file: './src/version.json'
            systemVersion = "${config.version}"
            def digits = systemVersion.tokenize('.')
            config['version'] = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"
            dockerLabel = "" + digits.get(0) + "." + digits.get(1) + "." + digits.get(2) + "." + "$BUILD_NUMBER"
            //writeJSON file: './src/version.json', json: config
        }
      }
    }

    stage("Building docker image"){
      steps{
        script {
          dockerImage = docker.build(registry + ":" + dockerLabel,"-f ./src/docker/web/Dockerfile ./")
        }
      }
    }

    stage("Push image to dockerhub"){
      steps{
        script {
          docker.withRegistry( '', registryCredential ) {
            dockerImage.push()
          }
        }
      }
    }

    stage('Deploy container to qa server') {
      steps {
        script {
            sh 'docker stop demo-django'
        }
        script {
            sh 'docker rm demo-django'
        }
        script {
            sh 'docker run -p 8085:80  --name demo-django -d ' + registry + ":" + dockerLabel + ' sh -c "cd src && /start"'
        }
      }  
    }
  }
}