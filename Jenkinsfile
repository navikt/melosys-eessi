#!groovy

//
// Forventer følgende build-parametere:
// - Miljo: Hvilken miljø (namespace) på NAIS som applikasjonen skal deployes til.

node {

    properties([
            parameters([
                    choice(choices: ['t8', 'default'],
                            description: 'Hvilket miljø skal applikasjon deployes til. Default er q', name: 'NAMESPACE')
            ])
    ])


    def KUBECTL = "/usr/local/bin/kubectl"
    def KUBECONFIG_NAISERATOR = "/var/lib/jenkins/kubeconfigs/kubeconfig-teammelosys.json"
    def NAISERATOR_CONFIG = "naiserator.yaml"
    def VERA_UPDATE_URL = "https://vera.adeo.no/api/v1/deploylog"
    def DEFAULT_BUILD_USER = "eessi2-jenkins"

    def cluster = "dev-fss"
    def dockerRepo = "docker.adeo.no:5000/melosys"
    def namespace = "${params.NAMESPACE}".toString()

    def mvnSettings = "navMavenSettingsUtenProxy"

    // pom related vars
    def application = "melosys-eessi"
    def imageVersion

    // Set Spring profiles to activate
    def springProfiles = "nais"

    stage("Checkout") {
        scmInfo = checkout scm
        commitId = scmInfo.GIT_COMMIT.substring(0, 10)
        imageVersion += new Date().format("YYYYMMddHHmmss") + "-" + commitId
        println("[INFO] imageVersion: ${imageVersion}")
    }

    stage("Build application") {
        configFileProvider([configFile(fileId: "$mvnSettings", variable: "MAVEN_SETTINGS")]) {
            sh "mvn clean package -Pcoverage -B -e -U -s $MAVEN_SETTINGS"
        }
    }

    stage("Build & publish Docker image") {
        configFileProvider([configFile(fileId: "$mvnSettings", variable: "MAVEN_SETTINGS")]) {
            sh "docker build --build-arg JAR_FILE=${application}-${imageVersion}.jar --build-arg SPRING_PROFILES=${springProfiles} -t ${dockerRepo}/${application}:${imageVersion} --rm=true ."
            sh "docker push ${dockerRepo}/${application}:${imageVersion}"
        }
    }

    stage("Deploy to NAIS") {
        prepareNaisYaml(NAISERATOR_CONFIG, imageVersion, namespace)

        // set namespace to context
        sh "${KUBECTL} config --kubeconfig=${KUBECONFIG_NAISERATOR} set-context ${cluster} --namespace=${namespace}"
        sh "${KUBECTL} config --kubeconfig=${KUBECONFIG_NAISERATOR} use-context ${cluster}"
        sh "${KUBECTL} apply --kubeconfig=${KUBECONFIG_NAISERATOR} -f ${NAISERATOR_CONFIG}"

        // Oppdater Vera
        try {
            def deployer = getBuildUser(DEFAULT_BUILD_USER)
            println("[INFO] Oppdaterer Vera => application=${application}, environment=${namespace}, version=${imageVersion}, deployedBy=${deployer}")
            sh "curl -i -s --header \"Content-Type: application/json\" --request POST --data \'{\"environment\": \"${namespace}\",\"application\": \"${application}\",\"version\": \"${imageVersion}\",\"deployedBy\": \"${deployer}\"}\' ${VERA_UPDATE_URL}"
        } catch (e) {
            println("[ERROR] Feil ved oppdatering av Vera. Exception: " + e)
        }
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
        return buildUser
    }
}

def prepareNaisYaml(naiseratorFile, version, namespace) {
    replaceInFile('@@RELEASE_VERSION@@', version, naiseratorFile)

    if (namespace == "default") {
        replaceInFile('@@URL_NAMESPACE@@', '', naiseratorFile)
    } else {
        replaceInFile('@@URL_NAMESPACE@@', "-${namespace}" as String, naiseratorFile)
    }

    replaceInFile('@@NAMESPACE@@', namespace, naiseratorFile)
}

def replaceInFile(oldString, newString, file) {
    sh "sed -i -e 's/${oldString}/${newString}/g' ${file}"
}