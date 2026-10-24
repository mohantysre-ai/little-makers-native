DELETE FROM progress;
DELETE FROM completion;
DELETE FROM inventory;
DELETE FROM equipped;
DELETE FROM ledger;
UPDATE wallet SET coins=0,stars=0 WHERE id=1;
