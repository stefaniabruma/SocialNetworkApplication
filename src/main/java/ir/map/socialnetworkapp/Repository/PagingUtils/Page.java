package ir.map.socialnetworkapp.Repository.PagingUtils;

import java.util.stream.Stream;

public interface Page<E> {

    PagingInformation getPagingInformation();
    PagingInformation getNextPagingInformation();
    Stream<E> getContent();

}
