package ir.map.socialnetworkapp.Repository.RepositoryInterfaces;

import ir.map.socialnetworkapp.Domain.Entity;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E>{

    Page<E> findAll(PagingInformation pagingInfo);
}
