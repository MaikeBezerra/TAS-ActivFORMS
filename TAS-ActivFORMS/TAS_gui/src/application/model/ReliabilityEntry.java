package application.model;

import java.math.BigDecimal;

import activforms.Probe;
import application.ExceptionCounter;
import application.MainGui;
import availability.Availability;
import constants.Constants;
import fault_tolerance.Fault_Tolerance;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import maturity.Maturity;
import recoverability.Recoverability;
import robustness.Robustness;

public class ReliabilityEntry {

	private SimpleStringProperty service;
	private SimpleIntegerProperty invocationNum;
	private SimpleIntegerProperty failNum;
	private SimpleDoubleProperty failRate;
	private SimpleDoubleProperty successRate;
	
	private int totalFalhas = 0;
	private int totalInvocations = 0;
	
	public static int falhasTotais;
	public static int totalF = 0;
	public static int numServ = 0;
	public static int temposDeResposta = 0;
	
	public ReliabilityEntry() {

	}

	public ReliabilityEntry(String service) {
		this.service = new SimpleStringProperty(service);
		this.invocationNum = new SimpleIntegerProperty(0);
		this.failNum = new SimpleIntegerProperty(0);
	}

	public ReliabilityEntry(String service, int invocationNum, int failNum) {
		this.service = new SimpleStringProperty(service);
		this.invocationNum = new SimpleIntegerProperty(invocationNum);
		this.failNum = new SimpleIntegerProperty(failNum);
	}

	public void setService(String service) {
		this.service = new SimpleStringProperty(service);
	}

	public String getService() {
		return service.get();
	}

	public void setInvocationNum(int invocationNum) {
		totalInvocations = totalInvocations + invocationNum;
		this.invocationNum = new SimpleIntegerProperty(invocationNum);
	}

	public int getInvocationNum() {
		return invocationNum.get();
	}

	public void setFailNum(int failNum) {
		this.failNum = new SimpleIntegerProperty(failNum);
		totalFalhas = totalFalhas + this.failNum.getValue();
		totalF = totalFalhas;
		calcularToleranciaAFalhas();
		calcularRobustez();
		//calcularDisponibilidade();
		calcularRecuperabilidade();
		
		falhasTotais = totalFalhas + ExceptionCounter.exception_counter;
	}

	public int getFailNum() {
		return failNum.get();
	}

	public void setRate() {
		BigDecimal bd = new BigDecimal(failNum.get() / (double) invocationNum.get());
		this.failRate = new SimpleDoubleProperty(bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		bd = new BigDecimal(1 - this.failRate.get());
		this.successRate = new SimpleDoubleProperty(bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	public double getFailRate() {
		return failRate.get();
	}

	public double getSuccessRate() {
		return successRate.get();
	}
		
	
	public int getTotalFalhas() {
		return totalFalhas;
	}

	public void setTotalFalhas(int totalFalhas) {
		this.totalFalhas = totalFalhas;
	}

	public int getTotalInvocations() {
		return totalInvocations;
	}

	public void setTotalInvocations(int totalInvocations) {
		this.totalInvocations = totalInvocations;
	}
		

	public void calcularToleranciaAFalhas() {
				
		int totalFalhasAdpt = Probe.falhasAdaptacaoNaoToleradas + Probe.falhasAdaptacaoToleradas;
		int falhasToleradasAdpt = Probe.falhasAdaptacaoToleradas;
		
		System.err.println("Falhas de adaptações toleradas " + falhasToleradasAdpt);
		System.err.println("Falhas de adptação totais " + totalFalhasAdpt);		
		
		//calcula a tolerancia de falhas por adapta��o (OK)
		Fault_Tolerance.adaptationFaultsTolerated(falhasToleradasAdpt, totalFalhasAdpt);
				
		//calcula o n�vel de tolerancia a falhas operacionais (OK)
		
		Fault_Tolerance.faultToleranceLevel(this.getTotalFalhas(), this.getTotalFalhas() + ExceptionCounter.exception_counter);
		System.out.println("Falhas toleradas:  "+ this.getTotalFalhas());
		System.out.println("Total de Falhas de invocação:" + this.getTotalFalhas());
		System.out.println("Falhas de exceção: " + ExceptionCounter.exception_counter);
		
		
		//calcula o n�mero de requisi��es respondidas na presen�a de uma falha (OK)
		//simular falha aqui 
		Fault_Tolerance.rateOfRequestsAnsweredInThePresenceOfSystemFailure(getTotalInvocations()-getTotalFalhas(),
				getTotalInvocations());
	}
	
	public void calcularRobustez() {
		int totalNumberOfAdaptations = Probe.adaptacoesCorretas + Probe.adaptacoesIncorretas;
		int numberOfCorrectAdaptations = Probe.adaptacoesCorretas;
		
		System.err.println("Adaptações Corretas: " + numberOfCorrectAdaptations);
		System.err.println("Adaptações Totais: " + totalNumberOfAdaptations);	
		//(OK) reproduzir a inconsist�ncia de contexto
		// tentar inputar mais de uma preferencia de QoS ao mesmo tempo
		Robustness.correctContextMismatchRate(numberOfCorrectAdaptations, totalNumberOfAdaptations);
		
	}
	
	public void calcularDisponibilidade() {
		
			
		int probeDowntime = Probe.probeDowntime; //downtime por adapta��o falha de adapta��o
		long screenDowntime = MainGui.screenDowntime; //screen downtime  por exception na tela principal
		int intScreenDowntime = (int)screenDowntime;
		int totalDownTime = probeDowntime + intScreenDowntime;
		
		int numberOfInterruptionsOrFaults = Probe.probeInterruptions + ExceptionCounter.exception_counter;
		int timeDivision = Constants.PER_SECOND;
		
		System.err.println("Probe Downtime : " + probeDowntime);
		System.err.println("Screen Downtime : " + screenDowntime);
		//Calcula o tempo m�dio que o sistema fica inativo por n�mero de interrup��es (quase OK)
		Availability.averageDowntime(totalDownTime, numberOfInterruptionsOrFaults, timeDivision);
		
		//calcula a taxa interrup��es por adapta��o (OK)
		Availability.adaptationInterruptionRate(Probe.probeInterruptions, Probe.probeInterruptions + ExceptionCounter.exception_counter);
		
		
		int totalNumberOfAdaptations = Probe.adaptacoesCorretas + Probe.adaptacoesIncorretas;
		int numberOfAdaptationsWithoutFaults = Probe.adaptacoesCorretas;
		
		System.err.println("Adaptações Sem Falhas: " + numberOfAdaptationsWithoutFaults);
		System.err.println("Adaptações Totais: " + totalNumberOfAdaptations);
		
		// calcula a taxa de adapta��es corretas durante a execu��o do sistema (OK)
		// Utilizar o sistema normalmente ao coletar essa medida
		Availability.correctAdaptationRate(numberOfAdaptationsWithoutFaults, totalNumberOfAdaptations);
		
		//calcula a taxa de requisi��es respondidas (OK)
		Availability.requestRequestsRate(getTotalInvocations()-getTotalFalhas(), getTotalInvocations());
		
		//(OK) Executar duas adapta��es em paralelo
		// calcula o tempo de resposta dado uma adapta��o ocorrendo em paralelo
		Availability.responseTimeOfARequestGivenTheOccurrenceOfAParallelAdaptation(Probe.serv.getResponseTime(),0);
		
		temposDeResposta += Probe.serv.getResponseTime();
		numServ++;
		System.err.println("TEMPO MDIO DE RESPOSTA: " +  (temposDeResposta/numServ));

	}
	
	//mean time between failures (OK)
	public static void mtbf() {
		
		Long timeOfOperation = MainGui.finalTime - MainGui.initialTime; 
		System.err.println("MEAN TIME BETWEEN FAILURES");	
		System.err.println("TEMPO DE OPERAÇÃO " + timeOfOperation);
		System.err.println("FALHAS DETECTADAS " + totalF);
		
		//tempo m�dio entre falhas (OK)
		if(totalF == 0) totalF = 1;
		
		System.err.println(timeOfOperation/totalF);
		Maturity.meanTimeBetweenFailure(timeOfOperation, totalF, Constants.PER_SECOND);
	}
	
	//mean time without failures (maturidade) (ok)
	public static void mtwf() {	
		
		long totalTimeOfOperation = MainGui.finalTime - MainGui.initialTime; 
		
		System.err.println(Probe.adptFinalRecoveryTime().size());
		System.err.println(Probe.adptInitRecoveryTime().size());
		
		if (!Probe.adptFinalRecoveryTime().equals(null) && !Probe.adptInitRecoveryTime().equals(null)) {
			Maturity.meanTimeWithoutFailure(totalTimeOfOperation, Probe.adptFinalRecoveryTime(), Probe.adptInitRecoveryTime());
			
		} else {
			System.err.println("Nenhuma falha foi recuperada");
		}	
	}
	
	public void calcularRecuperabilidade() {
		
		int totalFalhasAdpt = Probe.falhasAdaptacaoNaoToleradas + Probe.falhasAdaptacaoToleradas;
		
		System.err.println("CONTADOR DE FALHAS DE ADAPTAÇÃO RECUPERADAS :  " + Probe.countRecovery);
		System.err.println("CONTADOR DE TODAS AS FALHAS DE ADAPTAÇÃO:  " + totalFalhasAdpt);
		
		// calcula a taxa de recupera��o de adapta��es
		Recoverability.adaptationFailureRecoveryRate(Probe.countRecovery, totalFalhasAdpt);
		Recoverability.operatingDisasterRecoveryRate(0, getTotalFalhas());	
	}
			
	public static void meanRecovery() {

	}	
}
