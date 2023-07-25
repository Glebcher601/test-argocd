def getChangedFiles(changeSets) {
    return changeSets.collectMany { it.items.collectMany { item -> item.affectedFiles } }
}
def definedEnvs = ['dev', 'sandbox', 'qa', 'staging', 'prod']

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
                script {
                    def changedFiles = getChangedFiles(currentBuild.changeSets)
                    definedEnvs.each { definedEnv ->
                        if(changedFiles.any { it.getPath().startsWith(definedEnv) }) {
                            echo "Generating plan for $definedEnv"
                        }
                    }
                }
            }
        }
        stage('Build release') {
            when {
                tag pattern: "release\\/(qa|stage|prod)\\/.*", comparator: "REGEXP"
            }
            steps {
                echo 'Building tag release...'
                script {
                    def pattern = /release\/(?<env>qa|stage|prod)\/.*/
                    def matcher = (env.TAG_NAME =~ pattern)
                    matcher.matches()
                    def envResult = matcher.group("env")
                    echo "Releasing on env $envResult"
                }
            }
        }
    }
}