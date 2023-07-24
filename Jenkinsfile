pipeline {
    agent any
    stages {
        stage('Build PR') {
            when {
                branch pattern: "feature.*", comparator: "REGEXP"
            }
            steps {
                echo 'Building PR...'
            }
        }
        stage('Build main') {
            when {
                branch "main"
            }
            steps {
                echo 'Building Main branch...'
            }
        }
        stage('Build release') {
            when {
                tag pattern: "release\\/(qa|stage|prod)\\/.*", comparator: "REGEXP"
            }
            steps {
                echo 'Building tag release...'
            }
        }
    }
}