package pw.pojava.projekt.DopplerMain;

import java.util.List;

import javax.swing.SwingWorker;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
//Z TEJ KLASY DZIEDZICZA 2 PANELE DO TWORZENIA OBRAZU DZWIEKU DLA OBSERWATOROW
public abstract class ObserverAnimationPanel extends ChartPanel {
	
	private static final long serialVersionUID = 1L;
	JFreeChart chart;
	GUI gui;//referencja do GUI	
	XYSeries xySeries;	
	ObserverSwingWorker worker;	
	Double x,y,f;	
	Double pi = Math.PI;
	double timeDelay = 0.0; //czas zanim fala dotrze do obserwatora
	double timeRunaway = 0.0;//czas po ktorym obserwator ucieknie od fali
		
	public ObserverAnimationPanel(GUI gui, JFreeChart chart) {
		super(chart);
		this.chart = chart;		
		this.gui = gui;	
		
	}
	
	public class DataToSimulate{//klasa przechowujaca dane przesylane do processa - punkt wykresu i czestotliwosc chwilowa dzwieku
		XYDataItem xyDataItem;
		Double freq;
		
		DataToSimulate(XYDataItem xy, Double f){
			xyDataItem = xy;
			freq = f;
		}
		public XYDataItem getXY() {return xyDataItem;}
		public Double getFreq() {return freq;}
	}
	
	public abstract class ObserverSwingWorker extends SwingWorker<Void,DataToSimulate>{
			
			double time =0;//aktualna chwila czasu [ms]
			int sleep = 1;//ms ile spi
			int maxCount = 3000;//maksymalna liczba punktow na wykresie * freq
						
			ObserverSwingWorker(){
				xySeries.clear(); //usuwa wszystkie dane z serii
				for(int i=0; i<maxCount/((double)gui.soundFreq/100);i++) {
					xySeries.add(time-i,0.0);//wype�nia zerami dane
				}
				countTimeDelayAndRunaway();//oblicza czasy dotarcia fali i kiedy obserwator juz nie slyszy fali	
				//debugging
				//System.out.println(timeDelay);
				//System.out.println(timeRunaway);
			}
											
			//UWAGA: jest dzielenie freq przez 100, zeby pracowalo dla szerszego zakresu czestotliwosci - dzieki temu jest do 10-15kHzkHz, a nie do 100-150Hz
			@Override
	 	   	protected void process(List<DataToSimulate> data) {//dodaje dane do serii i jak jest ich za duzo to usuwa
	 		   	for(DataToSimulate d : data) {
	 		   		xySeries.add(d.getXY());
	 		   	while(xySeries.getItemCount()>maxCount/((double)gui.soundFreq/100))//if(xySeries.getItemCount()>500)//jak sie zmieni wartosc maxCount, to szerokosc inna
	 		   			xySeries.remove(0);	//to na gorze co zakomentowane jesli ma sie nie dostosowywac do czestotliwosci szerokosc okna 
	 		   	}
	 	   	}
			@Override
			abstract protected Void doInBackground() throws Exception; //oblicza wartosi sinusa, czas w ms i przesyla do process 								
		}
		abstract void countTimeDelayAndRunaway();//liczy czasy opoznienia i ucieczki
		public abstract void newWorker(); //metoda do tworzenia nowego swing workera
		
		//katy jak w specyfikacji we wzorze					
		//zwracaja chwilowa predkosc 
		//ponizsze metody uwzgledniaja wzgledne polozenie obserwatora i zrodla
		public abstract double getVObserver();//zwraca skladowa predkosci obserwatora wzdluz linii laczacej go ze zrodlem			
		public abstract double getPhiObserver();//zwraca kat miedzy wektorem predkosci obserwatora a linia laczaca zrodlo z obserwatorem		
		public abstract double getPhiSource();//zwraca kat miedzy wektorem predkosci zrodla a linia laczaca zrodlo z obserwatorem			
		
		public double getVSource() {//zwraca skladowa predkosci zrodla wzdluz linii laczacej je z obserwatorem
			double vSource = gui.pAnimation.source.getVx()*Math.cos(getPhiSource()) + gui.pAnimation.source.getVy()*Math.sin(getPhiSource());			
			return vSource;			
		}
			
		public double module(double m) {//zwraca modul liczby
			if(m >= 0) return m;
			else return (-m);
		}
}
