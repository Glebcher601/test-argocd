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
                    def pathPattern = /(?<env>dev|sandbox|qa|staging|prod)\/(?<path>.*)\/.*/
                    def local_branch = sh (
                            script: "git rev-parse --abbrev-ref HEAD",
                            label: "Getting current branch name",
                            returnStdout: true
                    ).trim()
                    println "Local branch is ${local_branch}"
                    def base_branch = env.CHANGE_TARGET
                    println "Base branch is ${base_branch}"
                    sh script: "git fetch origin --no-tags ${base_branch}", label: "Getting base branch"
                    def gitDiff = sh (
                            script: "git diff --name-only origin/${base_branch}..${local_branch}",
                            returnStdout: true
                    ).trim().readLines()
                    println "Git diff: $gitDiff"
                    def groupedPaths = gitDiff.groupBy {
                        def matcher = (it =~ pathPattern)
                        if (matcher.matches()) {
                            return matcher.group("env")
                        }
                        return "unknown"
                    }.collectEntries {
                        def envPathValues = it.value.collect {
                            def matcher = (it =~ pathPattern)
                            if (matcher.matches()) {
                                return matcher.group("path")
                            }
                            return ""
                        }.findAll { it != "" }
                        [(it.key): envPathValues]
                    }
                    println "Grouped: $groupedPaths"
                }
            }
        }
        stage('Build main') {
            when {
                branch "main"
            }
            steps {
                echo 'Building Main branch...'
                script {
                    def uuid1 = UUID.randomUUID()
                    def uuid2 = UUID.randomUUID()
                    sh "mkdir STSH$uuid1 && echo $uuid1 > STSH$uuid1/package.txt"
                    sh "mkdir STSH$uuid2 && echo $uuid2 > STSH$uuid2/package.txt"
                    stash name: "artifact", includes : "STSH*/package.txt"
                }
            }
        }
        stage('Unstash main') {
            when {
                branch "main"
            }
            steps {
                echo 'Unstash Main branch...'
                script {
                    unstash name: "artifact"
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