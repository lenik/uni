package net.bodz.bas.data.block;

public interface IMappedBlockManager {

    void addBlock(IMappedBlock block);

    void removeBlock(IMappedBlock block);

    void removeBlocks(int offset, int length);

    IMappedBlock findBlock(int offset);

    IMappedBlock findBlockBefore(IMappedBlock block);

    IMappedBlock findBlockAfter(IMappedBlock block);

    Iterable<IMappedBlock> findBlocksWithIn(int offset, int length);

    Iterable<IMappedBlock> findBlocksContains(int offset, int length);

}
