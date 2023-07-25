import java.util.stream.Collectors

def getChangedFiles(changeSet) {
    def result = []
//    changeSet.collectMany { it.items.collectMany { item -> item.affectedFiles } }
//    for (int i = 0; i < changeSet.size(); i++) {
//        def entries = changeSet[i].items
//        for (int j = 0; j < entries.length; j++) {
//            def entry = entries[j]
//            def files = new ArrayList(entry.affectedFiles)
//            for (int k = 0; k < files.size(); k++) {
//                def file = files[k]
//                result += file
//            }
//        }
//    }

    return result
}

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
                    def changeLogSets = currentBuild.changeSets
                    echo("changeSets=" + changeLogSets)
                    def changedFiles = getChangedFiles(currentBuild.changeSets)
                    def changed2 = currentBuild.changeSets.collectMany { it.items.collectMany { item -> item.affectedFiles } }
                    //echo("${[["1", "2"], ["3", "4"]].collectMany { it }}")
                    echo("${changed2}")
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