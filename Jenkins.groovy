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

    stage('Deploy container to production') {
      steps{
            script {
                def remote = [:]
                remote.name = "macbookpro"
                remote.host = "192.168.0.13"
                remote.allowAnyHosts = true
                withCredentials([usernamePassword(credentialsId: 'prodServer', passwordVariable: 'password', usernameVariable: 'userName')]) {
                        
                    remote.user = userName
                    remote.password = password
                    sshCommand remote: remote, command: 'docker pull ' + registry + ":" + dockerLabel
                    sshCommand remote: remote, command: 'docker container stop django_web'
                    sshCommand remote: remote, command: 'docker container rm django_web'
                    sshCommand remote: remote, command: 'docker run -p 8081:80 -d ' + registry + ":" + dockerLabel
                    
                }
            }
          
      }  
    }

    
  }
}