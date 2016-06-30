package siniflar;

/**
 * Created by mkatr on 30.6.2016.
 */
public class Kategoriler {

    private String CatogryId,CatogryName;

    public Kategoriler(String catogryId,String catogryName) {
        CatogryId = catogryId;
        CatogryName = catogryName;
    }

    public String getCatogryId() {
        return CatogryId;
    }

    public void setCatogryId(String catogryId) {
        CatogryId = catogryId;
    }

    public String getCatogryName() {
        return CatogryName;
    }

    public void setCatogryName(String catogryName) {
        CatogryName = catogryName;
    }
}
