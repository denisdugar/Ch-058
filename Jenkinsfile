pipeline{
    agent any
    
    stages{
        
        stage('checkout'){
            
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/dev']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/denisdugar/Ch-058']]])
                sh 'ls'
            }
        }   
    }
}
