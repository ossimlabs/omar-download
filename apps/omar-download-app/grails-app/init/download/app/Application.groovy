package download.app

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.netflix.hystrix.EnableHystrix


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.appinfo.InstanceInfo.InstanceStatus

@EnableHystrix
@EnableCircuitBreaker
@EnableDiscoveryClient

class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @HystrixCommand(fallbackMethod = "statusNotFound")
    public InstanceStatus notificationsStatus() {
        return discoveryClient.getNextServerFromEureka("OMAR-DOWNLOAD", false)
                .getStatus();
    }

    public InstanceStatus statusNotFound() {
        return InstanceStatus.DOWN;
    }

}
