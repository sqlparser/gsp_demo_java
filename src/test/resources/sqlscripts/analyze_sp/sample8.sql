USE [Retail]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetKoliInfoByBarkod]    Script Date: 05/30/2012 09:56:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER proc [dbo].[usp_GetKoliInfoByBarkod]    
 @ToplamID int,      
 @KoliID int,  
 @Barkod nvarchar(50)   
as       
      
declare @TemaTakipNo bigint, @SevkID int , @Depo dt_Depo      
      
select @TemaTakipNo=TemaTakipNo, @SevkID=SevkID, @Depo=Depo      
from tb_KargoKoliBaslik (nolock) where ToplamaID=@ToplamID and KoliID=@KoliID       
      
if object_id('tempdb..#tmpIrsaliye')>0      
 drop table #tmpIrsaliye      
      
select b.TemaTakipNo, b.SevkID, db.FromDepo, db.ToDepo, d.UrunID UrunID1, r.UrunID2, d.Miktar AsortiMiktar, r.Miktar ReceteMiktar      
into #tmpIrsaliye      
from tb_KargoKoliBaslik b (nolock)      
inner join tb_DepoSevkBaslik db (nolock) on db.FromDepo=b.Depo and db.SevkID=b.SevkID      
inner join tb_KargoKoliDetay d (nolock) on b.TemaTakipNo=d.TemaTakipNo      
left join tb_UrunRecete r (nolock) on r.UrunID1=d.UrunID      
where b.SevkID=@SevkID and b.Depo=@Depo      
      
update #tmpIrsaliye set UrunID2 = UrunID1, ReceteMiktar=1 where UrunID2 is null      
      
      
-- select sum(AsortiMiktar*ReceteMiktar) KontrolMiktar
-- from #tmpIrsaliye i (nolock)
-- inner join tb_Urun u (nolock) on u.UrunID=i.UrunID2
-- where TemaTakipNo=@TemaTakipNo and u.Barkod = @Barkod
-- group by TemaTakipNo, u.Barkod