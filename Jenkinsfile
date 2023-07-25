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
                    def pathPattern = /(?<env>dev|sandbox|qa|stage|prod)\/(?<path>.*)\/.*/
                    def changedFiles = getChangedFiles(currentBuild.changeSets)
                    changedFiles.each { file -> echo "${file.getPath()}" }
                    def shortened = changedFiles.collect {
                        def matcher = it.getPath() =~ pathPattern
                        if (matcher.matches()) {
                            return matcher.group("path")
                        }
                        return ""
                    }.findAll { it != "" }
                    echo "Shortened: ${shortened}"
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