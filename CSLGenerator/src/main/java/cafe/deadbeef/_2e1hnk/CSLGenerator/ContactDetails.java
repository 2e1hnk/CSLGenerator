package cafe.deadbeef._2e1hnk.CSLGenerator;

public class ContactDetails {
	private String callsign;
	private String locator;
	private String exchange;
	private int count;
	
	public ContactDetails(String callsign, String locator, String exchange, int count) {
		super();
		this.callsign = callsign;
		this.locator = locator;
		this.exchange = exchange;
		this.count = count;
	}
	
	public ContactDetails(String csvLine) {
		super();
		String[] details = csvLine.split(",");
		this.callsign = details[0];
		this.locator = details[1];
		this.exchange = details[2];
		this.count = Integer.parseInt(details[3]);
	}
	
	public String getCallsign() {
		return callsign;
	}
	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}
	public String getLocator() {
		return locator;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void incrementCount() {
		this.count++;
	}
	@Override
	public String toString() {
		return callsign + "," + locator + "," + exchange + "," + count;
	}
	
}
