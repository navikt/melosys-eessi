#!groovy

//
// Forventer følgende build-parametere:
// - Miljo: Hvilken miljø (namespace) på NAIS som applikasjonen skal deployes til.

node {
    def KUBECTL = "/usr/bin/kubectl"
    def KUBECONFIG_NAISERATOR = "/var/lib/jenkins/kubeconfigs/kubeconfig-teammelosys.json"
    def NAISERATOR_CONFIG = "naiserator.yaml"
    def VERA_UPDATE_URL = "https://vera.adeo.no/api/v1/deploylog"
    def DEFAULT_BUILD_USER = "Jenkins"

    def cluster = "preprod-fss"
    def dockerRepo = "docker.adeo.no:5000/melosys"

    def namespace = getParameter(params.Miljo, "default")

    def mvn = "${tool 'maven-3.5.0'}/bin/mvn".toString()
    def mvnSettings = "navMavenSettingsUtenProxy"

    configFileProvider([configFile(fileId: "$mvnSettings", variable: "MAVEN_SETTINGS")]) {
        sh "$mvn $arguments -s $MAVEN_SETTINGS"
    }

    // git related vars
    def branchName

    // pom related vars
    def pom, groupId, application, version, releaseVersion, isSnapshot

    // Set Spring profiles to activate
    def springProfiles = "nais"

    println("[INFO] namespace: ${namespace}")
    println("[INFO] springProfiles: ${springProfiles}")

    try {
        stage("Checkout") {
            scmInfo = checkout scm

            branchName = getCurrentBranch(scmInfo)

            // Pick pom vars
            pom = readMavenPom file: 'pom.xml'
            groupId = "${pom.groupId}"
            application = "${pom.artifactId}"
            version = "${pom.version}"
            releaseVersion = pom.version.tokenize("-")[0]
            isSnapshot = pom.version.contains("-SNAPSHOT")
            timeStamp = new Date().format("YYYYMMddHHmmss")
            releaseVersion += "-" + timeStamp

            println("[INFO] releaseVersion: ${releaseVersion}")
        }

        stage("Build application") {
            sh "mvn versions:set -B -DnewVersion=${releaseVersion} -DgenerateBackupPoms=false"
            sh "mvn clean package -Pcoverage -B -e -U"
        }

        stage("Build & publish Docker image") {
            sh "mvn clean package -DskipTests -B"
            sh "docker build --build-arg JAR_FILE=${application}-${releaseVersion}.jar --build-arg SPRING_PROFILES=${springProfiles} -t ${dockerRepo}/${application}:${releaseVersion} --rm=true ."
            sh "docker push ${dockerRepo}/${application}:${releaseVersion}"
        }

        stage("Deploy to NAIS") {
            prepareNaisYaml(NAISERATOR_CONFIG, releaseVersion, namespace)

            // set namespace to context
            sh "${KUBECTL} config --kubeconfig=${KUBECONFIG_NAISERATOR} set-context ${cluster} --namespace=${namespace}"

            sh "${KUBECTL} config --kubeconfig=${KUBECONFIG_NAISERATOR} use-context ${cluster}"
            sh "${KUBECTL} apply --kubeconfig=${KUBECONFIG_NAISERATOR} -f ${NAISERATOR_CONFIG}"

            // Oppdater Vera
            try {
                // Brukeren som skal registreres som deployer i Vera.
                def deployer = getBuildUser(DEFAULT_BUILD_USER)

                println("[INFO] Oppdaterer Vera => application=${application}, environment=${namespace}, version=${releaseVersion}, deployedBy=${deployer}")

                sh "curl -i -s --header \"Content-Type: application/json\" --request POST --data \'{\"environment\": \"${namespace}\",\"application\": \"${application}\",\"version\": \"${releaseVersion}\",\"deployedBy\": \"${deployer}\"}\' ${VERA_UPDATE_URL}"
            } catch (e) {
                println("[ERROR] Feil ved oppdatering av Vera. Exception: " + e)
            }
        }
    } catch (e) {
        println("[ERROR] " + e)

        throw e
    }
    finally {
    }
}

def getCurrentBranch(scmInfo) {
    if (scmInfo.GIT_BRANCH.startsWith('origin/')) {
        return scmInfo.GIT_BRANCH.substring(scmInfo.GIT_BRANCH.indexOf('/') + 1)
    } else {
        return scmInfo.GIT_BRANCH
    }
}

def getBuildUser(defaultUser) {
    def buildUser = defaultUser

    try {
        wrap([$class: 'BuildUser']) {
            buildUser = "${BUILD_USER} (${BUILD_USER_ID})"
        }
    } catch (e) {
        // Dersom bygg er auto-trigget, er ikke BUILD_USER variablene satt => defaultUser benyttes
    }
    return buildUser
}

def getParameter(paramValue, defaultValue) {
    return (paramValue != null) ? paramValue : defaultValue
}

def prepareNaisYaml(naiseratorFile, version, namespace) {
    // set version in yaml-file:
    replaceInFile('@@RELEASE_VERSION@@', version, naiseratorFile)

    // set namespace for ingress in yaml-file
    if (namespace == "default") {
        replaceInFile('@@URL_NAMESPACE@@', '', naiseratorFile)
    } else {
        replaceInFile('@@URL_NAMESPACE@@', "-${namespace}" as String, naiseratorFile)
    }

    // set namespace in metadata
    replaceInFile('@@NAMESPACE@@', namespace, naiseratorFile)
}

def replaceInFile(oldString, newString, file) {
    sh "sed -i -e 's/${oldString}/${newString}/g' ${file}"
}