#!/usr/bin/groovy

pipeline {
    tools {
        jdk "JDK8"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
        disableConcurrentBuilds()
    }
    environment {
        mavenOpts = "MAVEN_OPTS=-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"
    }
    agent {
        node {
            label 'hetzner-jenkins-slave-1'
        }
    }
    stages {
        stage('Get release version') {
            steps {
                script {
                    env.RELEASE_VERSION = sh(
                        script: '''
#!/bin/bash
mvn -f pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout | sed "s/-SNAPSHOT//"''',
                        returnStdout: true
                    )
                }
            }
        }
        stage('Build') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: '8d2e391d-ee95-40e7-ab17-9d1e21c09284',
                        usernameVariable: 'username',
                        passwordVariable: 'password'
                    )
                ]) {
                    sh '''
git config user.email "esbot@ardas.dp.ua"
git config user.name "$username"
git config user.password "$password"
export $mavenOpts
mvn -f pom.xml -B release:prepare release:perform -Dusername=$username -Dpassword=$password -Djavax.net.ssl.trustStore=/tmp
'''
                }
            }
        }
        stage("Set description") {
            steps {
                script {
                    currentBuild.description = "${env.RELEASE_VERSION}"
                }
            }
        }
    }
    post {
        failure {
            build(
                job: 'failure-build-notification',
                parameters: [
                    [
                        $class: 'StringParameterValue',
                        name: 'FAIL_JOB_NAME',
                        value: "$JOB_NAME",
                    ],
                    [
                        $class: 'StringParameterValue',
                        name: 'FAIL_BUILD_URL',
                        value: "$BUILD_URL",
                    ],
                    [
                        $class: 'StringParameterValue',
                        name: 'FAIL_GIT_COMMIT',
                        value: "$GIT_COMMIT",
                    ],
                    [
                        $class: 'StringParameterValue',
                        name: 'FAIL_GIT_URL',
                        value: "$GIT_URL",
                    ],
                ]
            )
            withCredentials([
                usernamePassword(
                    credentialsId: '8d2e391d-ee95-40e7-ab17-9d1e21c09284',
                    usernameVariable: 'username',
                    passwordVariable: 'password'
                )
            ]) {
                sh '''
mvn -f pom.xml release:rollback
mvn -f pom.xml release:clean
exit 1
'''
            }
        }
        always {
            cleanWs()
        }
    }
}
