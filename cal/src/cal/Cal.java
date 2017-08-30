package cal;
import java.util.*;
public class Cal {
	public static void main(String[] args){
		GregorianCalendar now=new GregorianCalendar();//������ǰ��������
		Date date=now.getTime();
		System.out.println(date.toString());//��ʱ�����ڶ����ַ�����ʽ��ӡ
		now.setTime(date);//���½�ʱ��������õ����ڶ�����
		int today=now.get(Calendar.DAY_OF_MONTH);
		int month=now.get(Calendar.MONTH);
		now.set(Calendar.DAY_OF_MONTH,1);//��������Ϊ���¿�ʼ����
		int week=now.get(Calendar.DAY_OF_WEEK);
		System.out.println(" sun   mon   tus   wed   thr   fri   sat");
		
		for(int i=Calendar.SUNDAY;i<week;i++) 
			System.out.print("      ");
		
		while(now.get(Calendar.MONTH)==month){
			int day=now.get(Calendar.DAY_OF_MONTH);
			if(day<10){
				if(day==today)
					System.out.print(" <"+day+">  ");
				else
					System.out.print("  "+day+"   ");
			}
			else{
				if(day==today)
					System.out.print("<"+day+">  ");
				else
					System.out.print(" "+day+"   ");
			}
			
			if(week==Calendar.SATURDAY){
				System.out.println();
			}
			now.add(Calendar.DAY_OF_MONTH,1);
			week=now.get(Calendar.DAY_OF_WEEK);
		}
	}
}
