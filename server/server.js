const express = require('express');
const { MongoClient, ObjectId } = require('mongodb'); // Sửa dòng này

const app = express();
const port = 3000; 

// Thay thế bằng chuỗi kết nối của bạn
const uri = "mongodb+srv://admin:1234@cluster0.pykrv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
const client = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });

app.use(express.json()); // Thêm middleware để xử lý JSON body

app.get('/api/items', async (req, res) => {
  try {
    await client.connect();
    const db = client.db('lab5');
    const collection = db.collection('distributor');
    const result = await collection.find({}).toArray();
    res.json({ status: 200, messenger: "Thành công", data: result }); 
  } catch (error) {
    console.error('Lỗi /api/items:', error);
    res.status(500).json({ status: 500, messenger: "Lỗi server", data: null });
  } 
});

app.get('/search-distributor', async (req, res) => {
    try {
      await client.connect();
      const db = client.db('lab5');
      const collection = db.collection('distributor');
  
      // Ép kiểu req.query.key thành chuỗi
      const key = String(req.query.key); 
  
      const result = await collection.find({ name: { $regex: key, $options: 'i' } }).toArray();
      res.json({ status: 200, messenger: "Thành công", data: result });
    } catch (error) {
      console.error('Lỗi /search-distributor:', error);
      res.status(500).json({ status: 500, messenger: "Lỗi server", data: null });
    } 
  }); 

  app.post('/add-distributor', async (req, res) => {
    try {
      await client.connect();
      const db = client.db('lab5');
      const collection = db.collection('distributor');
      const newDistributor = req.body;
  
      // Kiểm tra xem newDistributor có phải là object và có thuộc tính name hay không
      if (typeof newDistributor !== 'object' || !newDistributor.name) {
        return res.status(400).json({ status: 400, messenger: "Dữ liệu không hợp lệ", data: null });
      }
  
      // Thêm _id vào newDistributor 
      newDistributor._id = new ObjectId(); 
  
      const result = await collection.insertOne(newDistributor);
  
      res.json({ status: 200, messenger: "Thêm thành công", data: result.insertedId }); 
  
    } catch (error) {
      console.error('Lỗi /add-distributor:', error);
      res.status(500).json({ status: 500, messenger: "Lỗi server", data: null });
    } 
  });


  app.delete('/delete-distributor-by-id/:id', async (req, res) => {
    try {
      await client.connect();
      const db = client.db('lab5');
      const collection = db.collection('distributor');
  
      // Chuyển đổi distributorId sang ObjectId
      let distributorId;
      try {
        distributorId = new ObjectId(req.params.id);
      } catch (err) {
        return res.status(400).json({ status: 400, messenger: "Id không hợp lệ", data: null });
      }
  
      const result = await collection.deleteOne({ _id: distributorId }); 
      if (result.deletedCount === 1) {
        res.json({ status: 200, messenger: "Xóa thành công", data: null });
      } else {
        res.status(404).json({ status: 404, messenger: "Không tìm thấy nhà phân phối", data: null });
      }
    } catch (error) {
      console.error('Lỗi /delete-distributor-by-id:', error);
      res.status(500).json({ status: 500, messenger: "Lỗi server", data: null });
    } 
  });


  app.put('/update-distributor-by-id/:id', async (req, res) => {
    try {
      await client.connect();
      const db = client.db('lab5');
      const collection = db.collection('distributor');
  
      // Chuyển đổi distributorId sang ObjectId
      let distributorId;
      try {
        distributorId = new ObjectId(req.params.id);
      } catch (err) {
        return res.status(400).json({ status: 400, messenger: "Id không hợp lệ", data: null });
      }
  
      const updatedData = req.body;
  
      // Kiểm tra xem updatedData có phải là object và có thuộc tính cần cập nhật hay không
      if (typeof updatedData !== 'object' || Object.keys(updatedData).length === 0) {
        return res.status(400).json({ status: 400, messenger: "Dữ liệu cập nhật không hợp lệ", data: null });
      }
  
      const result = await collection.updateOne({ _id: distributorId }, { $set: updatedData }); 
      if (result.modifiedCount === 1) {
        res.json({ status: 200, messenger: "Cập nhật thành công", data: null });
      } else {
        res.status(404).json({ status: 404, messenger: "Không tìm thấy nhà phân phối", data: null });
      }
    } catch (error) {
      console.error('Lỗi /update-distributor-by-id:', error);
      res.status(500).json({ status: 500, messenger: "Lỗi server", data: null });
    } 
  });

app.listen(port, () => {
  console.log(`Ứng dụng đang lắng nghe tại http://localhost:${port}`);
});