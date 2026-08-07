SELECT TOP 5
       o.order_id,
       o.customer_id
FROM sales.orders AS o
WHERE o.status =
ORDER BY o.created_at DESC;
