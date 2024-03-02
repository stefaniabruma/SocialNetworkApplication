package ir.map.socialnetworkapp.Repository.PagingUtils;

import java.util.stream.Stream;

public class PageObject<T> implements Page<T>{

    private PagingInformation pagingInfo;
    private Stream<T> content;

    public PageObject(PagingInformation pagingInfo, Stream<T> content) {
        this.pagingInfo = pagingInfo;
        this.content = content;
    }

    @Override
    public PagingInformation getPagingInformation() {
        return pagingInfo;
    }

    @Override
    public PagingInformation getNextPagingInformation() {
        return new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize());
    }

    @Override
    public Stream<T> getContent() {
        return content;
    }
}
