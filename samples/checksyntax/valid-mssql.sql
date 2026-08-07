SELECT TOP 5
       o.order_id,
       o.customer_id,
       o.total_amount
FROM sales.orders AS o
WHERE o.status = 'OPEN'
ORDER BY o.created_at DESC;
