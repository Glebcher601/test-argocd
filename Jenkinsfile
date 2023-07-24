import java.util.stream.Collectors

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
                    echo([["1", "2"], ["3", "4"]].stream().flatMap { it.stream()}.collect(Collectors.toList()))
                    for (int i = 0; i < changeLogSets.size(); i++) {
                        def entries = changeLogSets[i].items
                        for (int j = 0; j < entries.length; j++) {
                            def entry = entries[j]
                            echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
                            def files = new ArrayList(entry.affectedFiles)
                            for (int k = 0; k < files.size(); k++) {
                                def file = files[k]
                                echo " ${file.editType.name} ${file.path}"
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