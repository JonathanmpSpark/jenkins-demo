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
            url: 'https://github.com/sparktechsllc/SPARKGAM.git',
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
    stage('Building image') {
        steps {
        //   script {
        //     dockerImage = docker.build(registry + ":" + dockerLabel,"-f ./dockerfiles/Webapp/Dockerfile ./")
        //   }
        }
    }
    stage('Upload docker image to registry') {
      steps{
    //     script {
    //       docker.withRegistry( '', registryCredential ) {
    //         dockerImage.push()
    //       }
    //     }
      }
    }
    stage('Deploy container to production') {
        steps{
        //     script {
        //         def remote = [:]
        //         remote.name = "prod"
        //         remote.host = "13.91.53.201"
        //         remote.allowAnyHosts = true
        //         withCredentials([usernamePassword(credentialsId: 'prodServer', passwordVariable: 'password', usernameVariable: 'userName')]) {
                        
        //             remote.user = userName
        //             remote.password = password
        //             sshCommand remote: remote, command: 'docker pull ' + registry + ":" + dockerLabel
        //             sshCommand remote: remote, command: 'docker container stop SGWebApp_Prod_https'
        //             sshCommand remote: remote, command: 'docker container rm SGWebApp_Prod_https'
        //             sshCommand remote: remote, command: 'docker run -p 443:443 -e ASPNETCORE_URLS="https://+" -e ASPNETCORE_HTTPS_PORT=443 -e ASPNETCORE_Kestrel__Certificates__Default__Password= -e ASPNETCORE_Kestrel__Certificates__Default__Path=/https/sparkgam.sparktechs.com.pfx -v ${HOME}/certificate/:/https/ --name SGWebApp_Prod_https -v webapp-user-files_prod:/app/wwwroot/Uploaded -d ' + registry + ":" + dockerLabel
                    
        //         }
        //     }
          
        }
        
    }
  }
}