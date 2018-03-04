	public class Quad {
		String opperation;
		String opperand1, opperand2, result;
		int op1nextuse,op2nextuse,op3nextuse;
		String address;
		public Quad(String o, String op1, String op2, String r) {
			opperation = o.trim();
			opperand1 = op1.trim();
			opperand2 = op2.trim();
			result = r.trim();;
			op1nextuse = -10;
			op2nextuse = -10;
			op3nextuse = -10;
			address = null;
			
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "" + opperation + ", " + opperand1 + ", " + opperand2 + ", " + result;
		}
		public String tofullString() {
			// TODO Auto-generated method stub
			return "" + opperation +", "+op1nextuse + ", " + opperand1 + ", "+op2nextuse +", " + opperand2 + ", " + ", "+op3nextuse +result;
		}
		public boolean isliveB() {
			if(op1nextuse == 0) {
				return false;
			}
			return true;
			
		}
		public boolean isliveC() {
			if(op2nextuse == 0) {
				return false;
			}
			return true;
		}
		public boolean BequalC(Symboltable st) {

			Nextusefield s1 = st.getFieldByName(opperand1), s2 = st.getFieldByName(opperand2);
			if(s1 == null || s2 == null) {
				return false;
			}
			if(s1.location.equals(s2.location)&&
					!st.getFieldByName(opperand1).location.equals("Memory")) {
				return true;
			}
			return false;
			
		}
	}