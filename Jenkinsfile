def getChangedFiles(changeSets) {
    return changeSets.collectMany { it.items.collectMany { item -> item.affectedFiles } }
}
def definedEnvs = ['dev', 'sandbox', 'qa', 'stage', 'prod']

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
                    def groupedPaths = getChangedFiles(currentBuild.changeSets).groupBy {
                        def matcher = it.getPath() =~ pathPattern
                        if (matcher.matches()) {
                            return matcher.group("env")
                        }
                        return "unknown"
                    }.collectEntries {
                        def envPathValues = it.value.collect {
                            def matcher = it.getPath() =~ pathPattern
                            if (matcher.matches()) {
                                return matcher.group("path")
                            }
                            return ""
                        }.findAll { it != "" }
                        [(it.key): envPathValues]
                    }
                    echo "GroupedFiltered: ${groupedPaths}"
                    definedEnvs.each { definedEnv ->
                        if(groupedPaths.containsKey(definedEnv)) {
                            echo "Generating plan for $definedEnv"
                            def includeDirs = groupedPaths[definedEnv].collect { "--terragrunt-include-dir ${it}"}.join(" ")
                            echo "IncludeDirs $includeDirs"
                            dir(definedEnv) {
                                sh "terragrunt plan $includeDirs --terragrunt-non-interactive -out=${definedEnv}.tfplan"
                            }
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