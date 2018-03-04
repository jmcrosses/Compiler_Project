	public class Nextusefield {
		String Name;
		String location;
		int nextuse;
		public Nextusefield(String o, int op1, String op2) {
			Name = o;
			nextuse = op1;
			location = op2;
		}
		public void updatenextuse(int nuse) {
			nextuse = nuse;
		}
		public void updatelocation(String nlocation) {
			location = nlocation;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "" + Name + ", " + nextuse + ", " + location + ", ";
		}
	}