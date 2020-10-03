# JavaCommon
Java Stuff

## Annotations
> annotaions like [NotNull](https://github.com/dushmis/commons/blob/master/com/dushyant/annotation/NotNull.java), [DBField.java](https://github.com/dushmis/commons/blob/master/com/dushyant/annotation/DBField.java), [Pattern](https://github.com/dushmis/commons/blob/master/com/dushyant/annotation/Pattern.java)

## Concurrent ObjectLock
> [ObjectLock](https://github.com/dushmis/commons/blob/master/com/dushyant/concurrent/ObjectLock.java) lock on specific object so other users can't use that object while locked object is being accessed by any process, any other object will get processed but same object will have to wait, or will timeout ( tryLock )

## Extractor 
> [Extractor](https://github.com/dushmis/commons/blob/master/com/dushyant/services/Extractor.java),[ExtractorService](https://github.com/dushmis/commons/blob/master/com/dushyant/services/ExtractorService.java), [Method](https://github.com/dushmis/commons/blob/master/com/dushyant/services/Method.java)

## Util
> [Util] with methods like [#measureTime](https://github.com/dushmis/commons/blob/master/com/dushyant/util/Util.java#L73) that takes a lambda using Consumer, Supplier interfaces
> [Util.use](https://github.com/dushmis/commons/blob/master/com/dushyant/util/Util.java#L166) borrow design pattern 

> ```
    final Optional<Map<?, ?>> use = Util.use(ConnectionPool.get(), closeable -> {
      //use closeable without worry
      return new HashMap<>();
    });
  ```

