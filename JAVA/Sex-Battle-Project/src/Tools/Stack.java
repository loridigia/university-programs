package Tools;

import Exceptions.EmptyListException;
import Exceptions.MaxIndexException;
import Exceptions.MinIndexException;

public class Stack<E> {
    
    
    public E                current;
    public Stack<E>         next;
    
    
    public Stack() {
        this.current = null;
        this.next = null;
    }
    
    
    public Stack(E current) {
        this.current = current;
        this.next = null;
    }
    
    
    public Stack(int capacity, E fillWith) {
        int temp = 0;
        while (temp<capacity) {
            this.add(fillWith);
            temp++;
        }
    }
    
    
    public E get(int i)  {

        try {
            if ( i < 0 )        throw new MinIndexException();
            if ( i > size() - 1 ) throw new MaxIndexException();
            int temp = 0;
            while (temp < i)  {
                return next.get(i-1);
            }
            return current;
            
        }  catch (MinIndexException e) {
            e.printStackTrace();
        } catch (MaxIndexException e) {
            e.printStackTrace();
        }
        return current;
    }
    
    
    public int size() {
        if (this.current==null) return 0;
        int i = 1;
        while (this.next != null) {
            return 1 + this.next.size();
        }
        return i;
    }
    
    
    public void add(E obj) {
        if (current == null) {
            current = obj;
            return;
        }
        if (next == null)
            next = new Stack<>(obj);
        else next.add(obj);
    }
    
    
    public E pop() {
          if (size() == 0) {
          	try {
          		throw new EmptyListException();
			} catch(EmptyListException e) {
			
			}
		  }
          
          if (size()==1) {
              E curr = current;
              current = null;
              return curr;
          }
          
          while (next.next != null) {
                return next.pop();
          }
          E curr = next.current;
          next = null;
          return curr;
    }
    
}
