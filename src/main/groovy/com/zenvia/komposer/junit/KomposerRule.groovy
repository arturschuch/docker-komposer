package com.zenvia.komposer.junit

import com.zenvia.komposer.model.Komposition
import com.zenvia.komposer.runner.KomposerRunner
import org.junit.rules.ExternalResource

/**
 * @author Tiago de Oliveira
 * */
class KomposerRule extends ExternalResource {

    private KomposerRunner runner
    private String composeFile
    private Map<String, Komposition> runningServices
    private pull = true
    private privateNetwork = false

    def KomposerRule(String compose, String dockerCfg, Boolean pull = true, Boolean privateNetwork = false) {
        this.runner = new KomposerRunner(dockerCfg, privateNetwork)
        this.composeFile = compose
        this.pull = pull
        this.privateNetwork = privateNetwork
    }

    def KomposerRule(String compose, Boolean pull = true) {
        this.runner = new KomposerRunner()
        this.composeFile = compose
        this.pull = pull
    }

    def KomposerRule(String compose, KomposerRunner runner) {
        this.runner = runner
        this.composeFile = composeFile
    }

    @Override
    void before() throws Throwable {
        this.runningServices = this.runner.up(this.composeFile, pull)
    }

    @Override
    void after() {
        this.runner.down(this.runningServices)
        this.runner.rm(this.runningServices)
        this.runner.finish()
    }

    def Map<String, Komposition> getContainers() {
        return this.runningServices
    }

    def stop(String serviceName){
        def containerId = runningServices[serviceName].containerId
        def containerInfo = this.runner.stop(containerId)
        this.runningServices[serviceName].containerInfo = containerInfo
    }

    def start(String serviceName){
        def containerId = runningServices[serviceName].containerId
        def containerInfo = this.runner.start(containerId)
        this.runningServices[serviceName].containerInfo = containerInfo
    }

    def URI getHostURI() {
        return this.runner.getHostUri()
    }
}
