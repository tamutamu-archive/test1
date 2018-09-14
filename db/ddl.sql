CREATE TABLE stock_code
  (stock_code VARCHAR(6) NOT NULL PRIMARY KEY, 
   name VARCHAR(50) NOT NULL,
   marcket VARCHAR(4)
  );


CREATE TABLE price
  (stock_code VARCHAR(6) NOT NULL, 
   stock_date DATE NOT NULL,
   open DECIMAL(14,2) NOT NULL,
   lower DECIMAL(14,2) NOT NULL,
   high DECIMAL(14,2) NOT NULL,
   close DECIMAL(14,2) NOT NULL,
   adjust DECIMAL(14,2) NOT NULL,
   vol BIGINT NOT NULL,
   PRIMARY KEY(stock_code,stock_date),
   FOREIGN KEY(stock_code) REFERENCES stock_code(stock_code)
  );

