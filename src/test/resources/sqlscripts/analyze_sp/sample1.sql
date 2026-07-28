USE [Retail]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateUrunIcerikForKoliKabul]    Script Date: 05/30/2012 09:56:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER proc [dbo].[usp_CreateUrunIcerikForKoliKabul] (@ProcessID bigint)    
    
as     
    
begin     
    
Create table #tmpKoliKabulCheck    
(    
 Barkod nvarchar(50),    
 IrsaliyeNo bigint,    
 GonderenDepo nvarchar(50),    
 AlanDepo nvarchar(50),  
 OkutmaTarihi nvarchar(50),  
 OkutanKullanici nvarchar(100)  
)    
    
insert into #tmpKoliKabulCheck    
exec usp_TemaMobileParseDataForKoliKabulCheck @ProcessID, 3    
    
update #tmpKoliKabulCheck set Barkod = replace(Barkod, '#', '_')    
    
select *,    
(select top 1 Data from dbo.fnk_Split(KoliBarkod, '_')) ToplamaId,    
(select top 1 Data from dbo.fnk_Split(KoliBarkod, '_') order by ID desc) ToplamaKoliId    
into #tmpCurs    
from (    
select UrunBarkod, KoliBarkod,     
COUNT(UrunBarkod) as FiiliMiktar, 0 KaydiMiktar from (    
SELECT TranData.value('(Barkod/@parentKey)[1]',    
  'varchar(max)') AS KoliBarkod,     
  TranData.value('(Barkod/@value)[1]',    
  'varchar(max)') AS UrunBarkod    
FROM tb_TemaMobileCommandTransaction where TemaMobileTransactionTipTanimRef = 1 and IsDeleted = 0
) k     
group by UrunBarkod, KoliBarkod    
) x    
where KoliBarkod in     
(select Barkod from #tmpKoliKabulCheck)    

    
    
DECLARE kolicursor Cursor    
FOR     
Select ToplamaId, ToplamaKoliId, UrunBarkod from #tmpCurs    
open kolicursor    
    
declare @m_ToplamaId int    
declare @m_ToplamaKoliId int    
declare @m_UrunBarkod nvarchar(50)    
    
fetch next from kolicursor into @m_ToplamaId, @m_ToplamaKoliId, @m_UrunBarkod    
While (@@FETCH_STATUS <> -1)    
BEGIN    
IF (@@FETCH_STATUS <> -2)    
begin    
     
 DECLARE @Output VARCHAR(100)    
    
 CREATE TABLE #tmpTable    
 (    
  OutputValue VARCHAR(100)    
 )    
 INSERT INTO #tmpTable (OutputValue)    
 EXEC usp_GetKoliInfoByBarkod @m_ToplamaId, @m_ToplamaKoliId, @m_UrunBarkod    
    
  if exists (select Top 1 1 from #tmpTable)  
  begin  
  SELECT    
   @Output = OutputValue  
  FROM     
   #tmpTable    
  end  
  else  
  begin  
 set @Output = 0
  end  
    
     
 DROP TABLE #tmpTable    
    
 update #tmpCurs set KaydiMiktar = @Output     
 where UrunBarkod = @m_UrunBarkod and ToplamaId = @m_ToplamaId and ToplamaKoliId = @m_ToplamaKoliId    
end    
    
FETCH NEXT FROM kolicursor INTO @m_ToplamaId, @m_ToplamaKoliId, @m_UrunBarkod    
END    
CLOSE kolicursor    
DEALLOCATE kolicursor    
    
select * from #tmpCurs  
  
end 


