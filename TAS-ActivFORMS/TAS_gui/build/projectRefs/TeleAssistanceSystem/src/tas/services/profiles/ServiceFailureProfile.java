/**
 * 
 */
package tas.services.profiles;

import java.util.Random;

import service.atomic.ServiceProfile;
import service.atomic.ServiceProfileAttribute;

/**
 * @author yfruan
 * @email  ry222ad@student.lnu.se
 *
 */
public class ServiceFailureProfile extends ServiceProfile {

	@ServiceProfileAttribute()
	public double failureRate;   // failure number in 100 invocation times
	
	//@ServiceProfileAttribute()
	//public double successRate;
	
	public ServiceFailureProfile(){}
	
	public ServiceFailureProfile(double failureRate){
		this.failureRate=failureRate;
	}
	
	@Override
	public boolean preInvokeOperation(String operationName, Object... args) {
	    System.out.println("PreInvokeOperation");
		Random rand=new Random();
		if(rand.nextDouble()<=failureRate){
			return false;
		}
		return true;
	}

	@Override
	public Object postInvokeOperation(String operationName, Object result,
			Object... args) {
		return result;
	}
}
