package ir.map.socialnetworkapp.Repository.PagingUtils;

public class PagingInformationObject implements PagingInformation{

    private int pageNumber;
    private int pageSize;

    public PagingInformationObject(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
