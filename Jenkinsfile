def getChangedFiles(changeSets) {
    return changeSets.collectMany { it.items.collectMany { item -> item.affectedFiles } }
}
def definedEnvs = ['dev', 'sandbox', 'qa', 'stage', 'prod']

pipeline {
    agent any
    stages {
        stage('Build PR') {
            when {
                expression {
                    env.CHANGE_ID && env.BRANCH_NAME.startsWith("PR-")
                }
            }
            steps {
                echo 'Building PR...'
                script {
                    def local_branch = sh (
                            script: "git rev-parse --abbrev-ref HEAD",
                            label: "Getting current branch name",
                            returnStdout: true
                    ).trim()
                    println "Local branch is ${local_branch}"

                    def base_branch = 'master'
                    // This is very naive.
                    // In reality, you need a better way to find out what your base branch is.
                    // One way is to have a file with a name of a base branch.
                    // Another one is to invoke API, e.g. GitHub API, to find out base branch.
                    // Use whatever works for you.
                    println "Base branch is ${base_branch}"

                    sh script: "git fetch origin --no-tags ${base_branch}", label: "Getting base branch"

                    def git_diff = sh (
                            script: "git diff --name-only origin/${base_branch}..${local_branch}",
                            returnStdout: true
                    ).trim()
                    println "Git diff: $git_diff"
                }
            }
        }
        stage('Build main') {
            when {
                branch "main"
            }
            steps {
                echo 'Building Main branch...'
//                script {
//                    def pathPattern = /(?<env>dev|sandbox|qa|stage|prod)\/(?<path>.*)\/.*/
//                    def groupedPaths = getChangedFiles(currentBuild.changeSets).groupBy {
//                        def matcher = it.getPath() =~ pathPattern
//                        if (matcher.matches()) {
//                            return matcher.group("env")
//                        }
//                        return "unknown"
//                    }.collectEntries {
//                        def envPathValues = it.value.collect {
//                            def matcher = it.getPath() =~ pathPattern
//                            if (matcher.matches()) {
//                                return matcher.group("path")
//                            }
//                            return ""
//                        }.findAll { it != "" }
//                        [(it.key): envPathValues]
//                    }
//                    echo "GroupedFiltered: ${groupedPaths}"
//                    definedEnvs.each { definedEnv ->
//                        if(groupedPaths.containsKey(definedEnv)) {
//                            echo "Generating plan for $definedEnv"
//                            def includeDirs = groupedPaths[definedEnv].collect { "--terragrunt-include-dir ${it}"}.join(" ")
//                            echo "IncludeDirs $includeDirs"
//                            dir(definedEnv) {
//                                sh "terragrunt plan $includeDirs --terragrunt-non-interactive -out=${definedEnv}.tfplan"
//                            }
//                        }
//                    }
//                }
                script {
                    def pattern = /release\/(?<env>qa|stage|prod)\/.*/
                    def matcher = ("release/qa/1.3" =~ pattern)
                    matcher.matches()
                    def envResult = matcher.group("env")
                    matcher = null
                    echo "Releasing on env $envResult"
                    dir(envResult) {
                        sh "ls"
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