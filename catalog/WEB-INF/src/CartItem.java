
import java.io.Serializable;

public class CartItem implements Serializable{

private String imgAddress;
private String name;
private int price;
private static final long serialVersionUID = 1L;

public String getImgAddress() {
 return imgAddress;
}

public String getName() {
 return name;
}

public int getPrice() {
 return price;
}

public void setImgAddress(String imgAddress) {
 this.imgAddress = imgAddress;
}

public void setName(String name) {
 this.name = name;
}

public void setPrice(int price) {
 this.price = price;
}

public CartItem(String imgAddress, String name, int price) {
 this.imgAddress = imgAddress;
 this.name = name;
 this.price = price;
}

@Override
public boolean equals(Object obj) {
 if (this == obj) {
  return true;
 }
 if (obj == null || obj.getClass() != this.getClass()) {
  return false;
 }

 CartItem classToCompare = (CartItem) obj;

 return classToCompare.getName().equals(this.getName());

}

@Override
public int hashCode() {
  return (int) this.name.hashCode();
}

}
