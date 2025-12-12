package ecommerce.shoestore.auth.address;

import org.hibernate.cache.internal.DisabledCaching;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private String province;
    private String district;
    private String commune;
    private String streetDetail;

    //GETTER AND SETTER
    public String getrovince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getStreetDetail() {
        return streetDetail;
    }

    public void setStreetDetail(String streetDetail) {
        this.streetDetail = streetDetail;
    }
}
